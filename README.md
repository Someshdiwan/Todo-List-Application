# 📝 Todo List Application

A lightweight **Spring Boot** web application designed to demonstrate clean backend architecture,  
Dockerized deployment, and both **Low-Level (LLD)** and **High-Level (HLD)** system design principles.

Built with **Java 17**, **Maven**, and **Docker**, it provides a command-based web UI for managing tasks interactively — directly in your browser.

---

```
### 🌐 Live Demo

```bash
🚀 Access the live app here (Render Free Tier):
https://todo-list-app-49q5.onrender.com/

🧩 About Deployment
• The application runs inside a Docker container hosted on Render Cloud.
• Render automatically pulls the latest image from the GitHub repository and builds it using a multi-stage Dockerfile (Maven build → JDK runtime).
• The backend is a Spring Boot service serving both the REST API and the interactive web terminal UI built with HTML + JavaScript.
• This deployment demonstrates a production-like environment on Render’s free tier (no manual server setup).

💻 Runtime Details
• Container base: eclipse-temurin:17-jdk-jammy
• Port exposed: 8080
• Auto-deployed from: Render
• Hosted endpoint: https://todo-list-app-49q5.onrender.com/

🧠 Key Features Live
• Interactive command-line style UI in browser
• Persistent REST endpoints for add, edit, delete, sort tasks
• Deployed using Spring Boot + Docker + Render CI workflow
```

---
## 🚀 Tech Stack

- **Java 17** — Core application logic
- **Spring Boot 3** — Backend framework
- **Maven** — Dependency management and build tool
- **HTML + Vanilla JavaScript** — Lightweight terminal-style web UI
- **Docker** — Containerized deployment
- **System Design** — Documentation for both LLD and HLD

---

## ⚙️ Features

- Add, edit, delete, toggle, and sort tasks
- Input validation for names and deadlines (`DD-MM-YYYY` format)
- Command-style browser UI (`add|name=...|deadline=...`)
- In-memory task management (no database needed)
- Global exception handling and clean API design
- Ready-to-run with Docker multi-stage build

---

## 🧱 Project Structure

```
Todo-List-Application/
│
├── .github/                      # GitHub workflows (CI/CD, optional)
├── Dockerfile                    # Docker multi-stage build configuration
├── pom.xml                       # Maven build & dependency configuration
├── LICENSE                       # Open-source license
├── README.md                     # Project documentation
├── .gitignore                    # Ignored build & IDE files
│
├── src/
│   └── main/
│       ├── java/
│       │   └── io/
│       │       └── yourname/
│       │           └── todo/
│       │               ├── WebApplication.java          # Spring Boot entry point
│       │               ├── TodoController.java          # REST endpoints for all commands
│       │               ├── TodoService.java             # Core business logic
│       │               ├── TodoItem.java                # Model class representing a task
│       │               ├── CreateTodoRequest.java       # DTO for creating new tasks
│       │               ├── UpdateTodoRequest.java       # DTO for updating existing tasks
│       │               └── GlobalExceptionHandler.java  # Unified exception handling
│       │
│       └── resources/
│           └── static/
│               └── index.html                           # Browser-based terminal UI
│
├── System Design/                 # Design documents & architecture artifacts
│   ├── src/
│   │   ├── Commands.txt           # Example commands for testing UI
│   │   ├── LLD.jpeg               # Low-Level Design diagram
│   │   ├── Low Level Design For ToDo List.jpeg  # Extended LLD reference
│   │   └── TodoListApplication    # Design project file (optional)
│   └── System Design.iml          # IntelliJ design project metadata
│
└── out/                           # Build output (excluded from Git)

```

---

## 🐳 Run with Docker

```bash
# Build Docker image
docker build -t todo-list-app:latest .

# Run container
docker run -p 8080:8080 todo-list-app:latest

Visit 👉 http://localhost:8080 to interact with the terminal-style UI.

⸻

🧭 System Design

Includes both Low-Level Design (LLD) and High-Level Design (HLD) documentation:
•	LLD: Internal class structure (TodoController, TodoService, DTOs, model)
•	HLD: System architecture showing backend, frontend, and Docker runtime layers
•	Visualized using Eraser.io Flowchart Generator for clear architecture mapping.

⸻

🎯 Learning Objectives

This project was created for hands-on exploration of:
•	Spring Boot REST API development
•	Dockerized builds and deployment pipelines
•	Applying LLD & HLD principles to real projects	
•	Clean code structure and modular design

⸻

🧠 Author

Somesh Diwan
GitHub: Someshdiwan

⸻

🏁 License

This project is released under the MIT License.
```

---
