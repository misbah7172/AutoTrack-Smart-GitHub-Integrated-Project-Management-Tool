# AutoTrack

AutoTrack is a smart GitHub-integrated project collaboration and task tracking tool. It automatically tracks your team's tasks directly from Git commit messages—no manual updates, no extra tools. Perfect for fast-paced development teams who want automated, real-time insights into their workflow.

## Features

- **GitHub Integration**: Automatically link GitHub repositories.
- **Team Management**: Create teams, assign members with nicknames.
- **Task Parsing from Commits**:
  - Commit example: `Feature01 : Login Page -> Misbah -> todo`
  - AutoTrack extracts task data from commits.
- **Automated Kanban Workflow**:
  - Tasks move from **TODO → IN PROGRESS → DONE** based on commits.
- **Real-time Sync**: See updates as they happen.
- **Activity Timeline**: Track progress, changes, and user activity.
- **Notifications**: Get notified when a task progresses or is completed.
- **Metrics Dashboard**: Visual reports for team performance.

## How It Works

1. **Create Repo**: Team lead creates a GitHub repo.
2. **Connect AutoTrack**: Repo is connected to AutoTrack.
3. **Add Team**: Team members are invited with email & nicknames.
4. **Assign Tasks via Commits**:
   ```bash
   git commit -m "Feature01 : Login Page -> Misbah -> todo"
5. Track Progress:

Any new commit on that feature = “In Progress”

A commit ending with -> done = marked “Done”




Commit Syntax Rules

Format	Meaning

Feature01 : Page -> Name -> todo	Adds task to TODO list
Feature01 : Page -> Name	Moves to IN PROGRESS
Feature01 : Page -> Name -> done	Marks as DONE


> Example: Feature03 : Backend API -> Misbah -> todo



Tech Stack

Frontend: JavaFX (for desktop) or Spring Boot + Thymeleaf (for web)

Backend: Java, Spring Boot

Database: MySQL / PostgreSQL

Integration: GitHub API


Team Setup

Each member has a nickname (used in commits)

Members commit from their own devices

AutoTrack continuously monitors GitHub activity


Installation

> (Coming soon: setup instructions for desktop/web deployment)



Contribution

Feel free to fork this repo and contribute. PRs welcome!

License

MIT License


---

AutoTrack – Write code. Commit. Track automatically.

---