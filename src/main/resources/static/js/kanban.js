/**
 * Kanban board functionality for task management
 */
document.addEventListener('DOMContentLoaded', () => {
    // Initialize drag and drop functionality for kanban cards
    initializeDragAndDrop();
    
    // Initialize task status update functionality
    initializeTaskStatusUpdate();
});

/**
 * Initialize drag and drop functionality for kanban cards
 */
function initializeDragAndDrop() {
    const draggables = document.querySelectorAll('.kanban-card');
    const dropzones = document.querySelectorAll('.kanban-column-body');
    
    // Add event listeners to draggable items
    draggables.forEach(draggable => {
        draggable.addEventListener('dragstart', () => {
            draggable.classList.add('dragging');
        });
        
        draggable.addEventListener('dragend', () => {
            draggable.classList.remove('dragging');
        });
    });
    
    // Add event listeners to dropzones
    dropzones.forEach(dropzone => {
        dropzone.addEventListener('dragover', e => {
            e.preventDefault();
            const dragging = document.querySelector('.dragging');
            if (dragging) {
                const afterElement = getDragAfterElement(dropzone, e.clientY);
                if (afterElement) {
                    dropzone.insertBefore(dragging, afterElement);
                } else {
                    dropzone.appendChild(dragging);
                }
            }
        });
        
        dropzone.addEventListener('drop', e => {
            e.preventDefault();
            const taskId = document.querySelector('.dragging').getAttribute('data-task-id');
            const newStatus = dropzone.closest('.kanban-column').getAttribute('data-status');
            
            if (taskId && newStatus) {
                updateTaskStatus(taskId, newStatus);
            }
        });
    });
}

/**
 * Get the element to insert the dragged item after
 */
function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll('.kanban-card:not(.dragging)')];
    
    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;
        
        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}

/**
 * Initialize task status update functionality
 */
function initializeTaskStatusUpdate() {
    // Add event listeners to status buttons
    const statusButtons = document.querySelectorAll('.status-btn');
    statusButtons.forEach(button => {
        button.addEventListener('click', () => {
            const taskId = button.getAttribute('data-task-id');
            const newStatus = button.getAttribute('data-status');
            
            if (taskId && newStatus) {
                updateTaskStatus(taskId, newStatus);
            }
        });
    });
}

/**
 * Update task status via AJAX
 */
function updateTaskStatus(taskId, newStatus) {
    const formData = new FormData();
    formData.append('status', newStatus);
    
    // Get CSRF token
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    
    fetch(`/tasks/${taskId}/status`, {
        method: 'POST',
        body: formData,
        headers: {
            [csrfHeader]: csrfToken
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update task status');
        }
        return response.json();
    })
    .then(data => {
        if (data.error) {
            showNotification(data.error, 'error');
        } else {
            showNotification('Task status updated successfully', 'success');
            
            // Optionally refresh the page after a short delay
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }
    })
    .catch(error => {
        showNotification(error.message, 'error');
    });
}

/**
 * Show notification message
 */
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : 'success'}`;
    notification.textContent = message;
    
    const container = document.querySelector('.container');
    container.insertBefore(notification, container.firstChild);
    
    // Remove notification after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}
