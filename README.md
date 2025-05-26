# AutoTrack

**AutoTrack** is a smart GitHub-integrated project collaboration and task tracking tool. It automates your workflow by extracting and tracking tasks from Git commit messages—no need for manual task updates. Ideal for development teams that want to focus on code and let the system handle tracking.

---

## Features

- **GitHub Integration**: Automatically connects to repositories.
- **Team Management**: Add team members by email with nicknames.
- **Commit-Based Task Management**:
  - Commit format:
    ```
    Feature01 : Login Page -> Misbah -> todo
    ```
  - AutoTrack parses and creates tasks from commits.
- **Kanban Board**:
  - Automatically updates task status:
    - `-> todo`: Task added to **TODO**
    - Commit without status: Task moved to **IN PROGRESS**
    - `-> done`: Task moved to **DONE**
- **Real-Time Updates**: Tracks commits and changes instantly.
- **Notifications**: Team gets notified of task progress.
- **Activity Log**: View full history of commits per feature/user.
- **Dashboard**: Visualize task status and team performance.

---

## Workflow

1. **Team Lead** creates a GitHub repo.
2. **Connect AutoTrack** to the repo.
3. **Add team members** using email and assign nicknames.
4. **Assign tasks** via commit messages:
    ```bash
    git commit -m "Feature01 : Login Page -> Misbah -> todo"
    ```
5. AutoTrack parses and updates the Kanban board automatically based on further commits.

---

## Commit Syntax Rules

| Format                                       | Description             |
|---------------------------------------------|-------------------------|
| `Feature01 : Task -> Misbah -> todo`        | Create TODO task        |
| `Feature01 : Task -> Misbah`                | Move to IN PROGRESS     |
| `Feature01 : Task -> Misbah -> done`        | Move to DONE            |

**Example:**
```bash
git commit -m "Feature03 : Backend API integration -> Misbah -> todo"
```

---

## Technology Stack

- **Frontend**: JavaFX (Desktop) / Spring Boot + Thymeleaf (Web)
- **Backend**: Java, Spring Boot
- **Database**: MySQL / PostgreSQL
- **Integration**: GitHub REST API

---

## Getting Started

> Setup instructions and deployment steps coming soon...

---

## Contribution

Contributions are welcome!  
Fork the repository, make your changes, and submit a pull request.

---

## License

This project is licensed under the **MIT License**.

---

**AutoTrack** – _Write code. Commit. Track automatically._