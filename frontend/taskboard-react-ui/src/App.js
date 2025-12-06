//
// import React, { useState } from "react";
// import "./App.css";
//
// import { apiCreateTask, apiMoveTask } from "./api";
//
// const INITIAL_TASKS = {
//   "1": {
//     id: "1",
//     title: "Set up project",
//     description: "Create Java + React skeleton",
//     priority: "HIGH",
//     dueDate: "2025-11-20",
//   },
//   "2": {
//     id: "2",
//     title: "Add design patterns",
//     description: "Command, State, Strategy, Factory, Observer",
//     priority: "MEDIUM",
//     dueDate: "",
//   },
//   "3": {
//     id: "3",
//     title: "Write report",
//     description: "Document patterns + architecture",
//     priority: "LOW",
//     dueDate: "",
//   },
// };
//
// const INITIAL_COLUMNS = {
//   "todo": {
//     id: "todo",
//     title: "To Do",
//     taskIds: ["1", "2"],
//   },
//   "in-progress": {
//     id: "in-progress",
//     title: "In Progress",
//     taskIds: ["3"],
//   },
//   "done": {
//     id: "done",
//     title: "Done",
//     taskIds: [],
//   },
// };
//
// const COLUMN_ORDER = ["todo", "in-progress", "done"];
//
// function App() {
//   const [tasks, setTasks] = useState(INITIAL_TASKS);
//   const [columns, setColumns] = useState(INITIAL_COLUMNS);
//
//   const [newTask, setNewTask] = useState({
//     title: "",
//     description: "",
//     priority: "MEDIUM",
//     columnId: "todo",
//     dueDate: "",
//   });
//
//   const handleDragStart = (event, taskId) => {
//     event.dataTransfer.setData("text/plain", taskId);
//   };
//
//   const handleDragOver = (event) => {
//     event.preventDefault();
//   };
//
//   // const handleDrop = (event, targetColumnId) => {
//   //   event.preventDefault();
//   //   const taskId = event.dataTransfer.getData("text/plain");
//   //   if (!taskId) return;
//   //
//   //   setColumns((prev) => {
//   //     let sourceColumnId = null;
//   //
//   //     for (const colId of Object.keys(prev)) {
//   //       if (prev[colId].taskIds.includes(taskId)) {
//   //         sourceColumnId = colId;
//   //         break;
//   //       }
//   //     }
//   //
//   //     if (!sourceColumnId || sourceColumnId === targetColumnId) {
//   //       return prev;
//   //     }
//   //
//   //     const newColumns = { ...prev };
//   //
//   //     newColumns[sourceColumnId] = {
//   //       ...newColumns[sourceColumnId],
//   //       taskIds: newColumns[sourceColumnId].taskIds.filter((id) => id !== taskId),
//   //     };
//   //
//   //     if (!newColumns[targetColumnId]) return prev;
//   //
//   //     newColumns[targetColumnId] = {
//   //       ...newColumns[targetColumnId],
//   //       taskIds: [...newColumns[targetColumnId].taskIds, taskId],
//   //     };
//   //
//   //     return newColumns;
//   //   });
//   // };
//
//     const handleDrop = (event, targetColumnId) => {
//         event.preventDefault();
//         const taskId = event.dataTransfer.getData("text/plain");
//         if (!taskId) return;
//
//         setColumns((prev) => {
//             let sourceColumnId = null;
//
//             for (const colId of Object.keys(prev)) {
//                 if (prev[colId].taskIds.includes(taskId)) {
//                     sourceColumnId = colId;
//                     break;
//                 }
//             }
//
//             if (!sourceColumnId || sourceColumnId === targetColumnId) {
//                 return prev;
//             }
//
//             const newColumns = { ...prev };
//
//             newColumns[sourceColumnId] = {
//                 ...newColumns[sourceColumnId],
//                 taskIds: newColumns[sourceColumnId].taskIds.filter((id) => id !== taskId),
//             };
//
//             if (!newColumns[targetColumnId]) return prev;
//
//             newColumns[targetColumnId] = {
//                 ...newColumns[targetColumnId],
//                 taskIds: [...newColumns[targetColumnId].taskIds, taskId],
//             };
//
//             return newColumns;
//         });
//
//         // Call backend to persist the move
//         apiMoveTask(taskId, targetColumnId).catch((e) => {
//             console.error("Failed to move task", e);
//             // Optionally: revert UI here if needed
//         });
//     };
//
//     const handleAddTask = async (event) => {
//       event.preventDefault();
//       if (!newTask.title.trim()) return;
//
//       const id = Date.now().toString();
//       const columnId = newTask.columnId || "todo"
//       const task = {
//           id,
//           title: newTask.title.trim(),
//           description: newTask.description.trim(),
//           priority: newTask.priority,
//           dueDate: newTask.dueDate.trim(),
//       };
//
//       setTasks((prev) => ({
//           ...prev,
//           [id]: task,
//       }));
//
//       setColumns((prev) => {
//           const col = prev[columnId] || prev["todo"];
//           // const col = prev[colId] || prev["todo"];
//           return {
//               ...prev,
//               [col.id]: {
//                   ...col,
//                   taskIds: [...col.taskIds, id],
//               },
//           };
//       });
//
//       setNewTask({
//           title: "",
//           description: "",
//           priority: "MEDIUM",
//           columnId: "todo",
//           dueDate: "",
//       });
//
// // inside handleAddTask, after you update local state:
//       await apiCreateTask({
//           title: newTask.title,
//           description: newTask.description,
//           priority: newTask.priority,
//           dueDate: newTask.dueDate,
//           columnId//: newTask.columnId,  "todo" | "in-progress" | "done"
//       });
//
//       // await apiMoveTask(taskId, targetColumnId);
//
//   };
//
//   return (
//     <div className="app">
//       <header className="app-header">
//         <div>
//           <h1>TaskBoard UI</h1>
//           <p>Drag tasks between columns · Add new tasks with priority</p>
//         </div>
//       </header>
//
//       <section className="add-task">
//         <h2>Add Task</h2>
//         <form onSubmit={handleAddTask} className="add-task-form">
//           <input
//             type="text"
//             placeholder="Title"
//             value={newTask.title}
//             onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
//             required
//           />
//           <input
//             type="text"
//             placeholder="Description"
//             value={newTask.description}
//             onChange={(e) =>
//               setNewTask({ ...newTask, description: e.target.value })
//             }
//           />
//           <select
//             value={newTask.priority}
//             onChange={(e) =>
//               setNewTask({ ...newTask, priority: e.target.value })
//             }
//           >
//             <option value="HIGH">High</option>
//             <option value="MEDIUM">Medium</option>
//             <option value="LOW">Low</option>
//           </select>
//           <input
//             type="date"
//             value={newTask.dueDate}
//             onChange={(e) =>
//               setNewTask({ ...newTask, dueDate: e.target.value })
//             }
//           />
//           <select
//             value={newTask.columnId}
//             onChange={(e) =>
//               setNewTask({ ...newTask, columnId: e.target.value })
//             }
//           >
//             <option value="todo">To Do</option>
//             <option value="in-progress">In Progress</option>
//             <option value="done">Done</option>
//           </select>
//           <button type="submit">Add</button>
//         </form>
//       </section>
//
//       <main className="board">
//         {COLUMN_ORDER.map((columnId) => {
//           const column = columns[columnId];
//           const columnTasks = column.taskIds.map((taskId) => tasks[taskId]);
//
//           return (
//             <Column
//               key={column.id}
//               column={column}
//               tasks={columnTasks}
//               onDragOver={handleDragOver}
//               onDrop={handleDrop}
//               onDragStart={handleDragStart}
//             />
//           );
//         })}
//       </main>
//     </div>
//   );
// }
//
// function Column({ column, tasks, onDragOver, onDrop, onDragStart }) {
//   return (
//     <div
//       className="column"
//       onDragOver={onDragOver}
//       onDrop={(e) => onDrop(e, column.id)}
//     >
//       <div className="column-header">
//         <h2>{column.title}</h2>
//         <span className="column-count">{tasks.length}</span>
//       </div>
//       <div className="column-body">
//         {tasks.map((task) => (
//           <TaskCard key={task.id} task={task} onDragStart={onDragStart} />
//         ))}
//       </div>
//     </div>
//   );
// }
//
// function TaskCard({ task, onDragStart }) {
//   return (
//     <div
//       className={`task-card priority-${task.priority.toLowerCase()}`}
//       draggable
//       onDragStart={(e) => onDragStart(e, task.id)}
//     >
//       <div className="task-title">#{task.id} · {task.title}</div>
//       {task.description && (
//         <div className="task-description">{task.description}</div>
//       )}
//       <div className="task-meta">
//         <span className="badge">{task.priority}</span>
//         <span className="due">
//           {task.dueDate ? `Due: ${task.dueDate}` : "No due date"}
//         </span>
//       </div>
//     </div>
//   );
// }
//
// export default App;

import React, { useState, useEffect } from "react";
import "./App.css";

import { apiCreateTask, apiMoveTask, apiFetchTasks, apiDeleteTask } from "./api";

const EMPTY_COLUMNS = {
    todo: {
        id: "todo",
        title: "To Do",
        taskIds: [],
    },
    "in-progress": {
        id: "in-progress",
        title: "In Progress",
        taskIds: [],
    },
    done: {
        id: "done",
        title: "Done",
        taskIds: [],
    },
};

const COLUMN_ORDER = ["todo", "in-progress", "done"];

function App() {
    const [tasks, setTasks] = useState({});
    const [columns, setColumns] = useState(EMPTY_COLUMNS);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [newTask, setNewTask] = useState({
        title: "",
        description: "",
        priority: "MEDIUM",
        columnId: "todo",
        dueDate: "",
    });

    // 🔹 Load tasks from backend on mount
    useEffect(() => {
        loadTasksFromBackend();
    }, []);

    async function loadTasksFromBackend() {
        try {
            setLoading(true);
            setError(null);

            const backendTasks = await apiFetchTasks(); // [] if none

            const tasksById = {};
            const nextColumns = {
                todo: { ...EMPTY_COLUMNS.todo, taskIds: [] },
                "in-progress": { ...EMPTY_COLUMNS["in-progress"], taskIds: [] },
                done: { ...EMPTY_COLUMNS.done, taskIds: [] },
            };

            backendTasks.forEach((t) => {
                const id = String(t.id);
                tasksById[id] = {
                    id,
                    title: t.title,
                    description: t.description || "",
                    priority: t.priority || "MEDIUM",
                    dueDate: t.dueDate || "",
                };

                const colId =
                    t.columnId === "todo" ||
                    t.columnId === "in-progress" ||
                    t.columnId === "done"
                        ? t.columnId
                        : "todo";

                nextColumns[colId].taskIds.push(id);
            });

            setTasks(tasksById);
            setColumns(nextColumns);
        } catch (e) {
            console.error("Failed to load tasks", e);
            setError(e.message || "Failed to load tasks");
        } finally {
            setLoading(false);
        }
    }

    const handleDragStart = (event, taskId) => {
        event.dataTransfer.setData("text/plain", taskId);
    };

    const handleDragOver = (event) => {
        event.preventDefault();
    };

    const handleDeleteTask = async (taskId) => {
        try {
            await apiDeleteTask(taskId);
            // simplest: just reload board from backend
            await loadTasksFromBackend();
        } catch (e) {
            console.error("Failed to delete task", e);
        }
    };

    const handleDrop = (event, targetColumnId) => {
        event.preventDefault();
        const taskId = event.dataTransfer.getData("text/plain");
        if (!taskId) return;

        setColumns((prev) => {
            let sourceColumnId = null;

            for (const colId of Object.keys(prev)) {
                if (prev[colId].taskIds.includes(taskId)) {
                    sourceColumnId = colId;
                    break;
                }
            }

            if (!sourceColumnId || sourceColumnId === targetColumnId) {
                return prev;
            }

            const newColumns = { ...prev };

            newColumns[sourceColumnId] = {
                ...newColumns[sourceColumnId],
                taskIds: newColumns[sourceColumnId].taskIds.filter(
                    (id) => id !== taskId
                ),
            };

            if (!newColumns[targetColumnId]) return prev;

            newColumns[targetColumnId] = {
                ...newColumns[targetColumnId],
                taskIds: [...newColumns[targetColumnId].taskIds, taskId],
            };

            return newColumns;
        });

        // persist move in backend (id is numeric in Java, but here it's a string, so backend will parse int)
        apiMoveTask(taskId, targetColumnId).catch((e) => {
            console.error("Failed to move task", e);
            // Optional: you could reload from backend or revert state here
        });
    };

    const handleAddTask = async (event) => {
        event.preventDefault();
        if (!newTask.title.trim()) return;

        const columnId = newTask.columnId || "todo";

        try {
            // send to backend – backend will assign ID and always put in "To Do" column
            await apiCreateTask({
                title: newTask.title.trim(),
                description: newTask.description.trim(),
                priority: newTask.priority,
                dueDate: newTask.dueDate.trim(),
                columnId, // backend currently ignores this, but ok to send
            });

            // 🔹 Reload from backend so IDs & columns are correct
            await loadTasksFromBackend();

            // reset form
            setNewTask({
                title: "",
                description: "",
                priority: "MEDIUM",
                columnId: "todo",
                dueDate: "",
            });
        } catch (e) {
            console.error("Failed to create task", e);
            setError(e.message || "Failed to create task");
        }
    };

    return (
        <div className="app">
            <header className="app-header">
                <div>
                    <h1>TaskBoard UI</h1>
                    <p>Drag tasks between columns · Add new tasks with priority</p>
                </div>
            </header>

            <section className="add-task">
                <h2>Add Task</h2>
                <form onSubmit={handleAddTask} className="add-task-form">
                    <input
                        type="text"
                        placeholder="Title"
                        value={newTask.title}
                        onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Description"
                        value={newTask.description}
                        onChange={(e) =>
                            setNewTask({ ...newTask, description: e.target.value })
                        }
                    />
                    <select
                        value={newTask.priority}
                        onChange={(e) =>
                            setNewTask({ ...newTask, priority: e.target.value })
                        }
                    >
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                    <input
                        type="date"
                        value={newTask.dueDate}
                        onChange={(e) =>
                            setNewTask({ ...newTask, dueDate: e.target.value })
                        }
                    />
                    <select
                        value={newTask.columnId}
                        onChange={(e) =>
                            setNewTask({ ...newTask, columnId: e.target.value })
                        }
                    >
                        <option value="todo">To Do</option>
                        <option value="in-progress">In Progress</option>
                        <option value="done">Done</option>
                    </select>
                    <button type="submit">Add</button>
                </form>
                {error && <div className="error">Error: {error}</div>}
            </section>

            <main className="board">
                {loading ? (
                    <div style={{ padding: "1rem" }}>Loading tasks from backend…</div>
                ) : (
                    COLUMN_ORDER.map((columnId) => {
                        const column = columns[columnId];
                        const columnTasks = column.taskIds
                            .map((taskId) => tasks[taskId])
                            .filter(Boolean);

                        return (
                            <Column
                                key={column.id}
                                column={column}
                                tasks={columnTasks}
                                onDragOver={handleDragOver}
                                onDrop={handleDrop}
                                onDragStart={handleDragStart}
                                onDeleteTask={handleDeleteTask}
                            />
                        );
                    })
                )}
            </main>
        </div>
    );
}

function Column({ column, tasks, onDragOver, onDrop, onDragStart, onDeleteTask }) {
    return (
        <div
            className="column"
            onDragOver={onDragOver}
            onDrop={(e) => onDrop(e, column.id)}
        >
            <div className="column-header">
                <h2>{column.title}</h2>
                <span className="column-count">{tasks.length}</span>
            </div>
            <div className="column-body">
                {tasks.map((task) => (
                    <TaskCard key={task.id} task={task} onDragStart={onDragStart}  onDeleteTask={onDeleteTask}/>
                ))}
            </div>
        </div>
    );
}

function TaskCard({ task, onDragStart, onDeleteTask }) {
    return (
        <div
            className={`task-card priority-${task.priority.toLowerCase()}`}
            draggable
            onDragStart={(e) => onDragStart(e, task.id)}
        >
            <div className="task-title">
                #{task.id} · {task.title}
            </div>
            {task.description && (
                <div className="task-description">{task.description}</div>
            )}
            <div className="task-meta">
                <span className="badge">{task.priority}</span>
                <span className="due">
          {task.dueDate ? `Due: ${task.dueDate}` : "No due date"}
        </span>
            </div>
            <button
                className="delete-btn"
                onClick={() => onDeleteTask(task.id)}
                type="button"
            >
                ✕
            </button>
        </div>
    );
}

export default App;