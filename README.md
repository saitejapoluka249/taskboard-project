# TaskBoard Project

This is a full-stack Kanban board application consisting of a Java Spark backend and a React frontend.

## Prerequisites
* **Java 21** (Recommended).
* **IMPORTANT:** You must use a Java version **below Java 25**. (Java 25 is currently incompatible with Gradle and will cause build errors).
* **Node.js & npm** (Required for the frontend).

---

## How to Run the Project

You need to run the **Backend** and the **Frontend** in two separate terminals.

### 1. Start the Backend (Java)
The backend runs on port `9090`.

1.  Open a terminal and navigate to the `backend` folder:
    ```bash
    cd backend
    ```

2.  Grant execution permission to the Gradle wrapper (Mac/Linux only):
    ```bash
    chmod +x gradlew
    ```

3.  Run the application:
    * **Mac/Linux:**
        ```bash
        ./gradlew run
        ```
    * **Windows:**
        ```bash
        .\gradlew.bat run
        ```

*Wait until you see: `TaskBoard HTTP API running on http://localhost:9090`*

---

### 2. Start the Frontend (React)
The frontend runs on port `3000`.

1.  Open a **new** terminal tab and navigate to the frontend folder:
    ```bash
    cd frontend/taskboard-react-ui
    ```

2.  Install dependencies:
    ```bash
    npm install
    ```

3.  Start the React server:
    ```bash
    npm start
    ```

4.  The application should automatically open in your browser at:
    [http://localhost:3000](http://localhost:3000)

---

## Troubleshooting

* **Build Failed (Class Version Error):** If you see "Unsupported class file major version 69", it means you are using **Java 25**. Please switch to **Java 21**.
* **Port In Use:** If you see an error about port 9090 or 3000 being in use, make sure to stop any previous instances of the application using `Ctrl+C`.