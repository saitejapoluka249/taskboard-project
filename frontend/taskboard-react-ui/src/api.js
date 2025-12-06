const API_BASE = "http://localhost:9090";

export async function apiCreateTask(task) {
    await fetch(`${API_BASE}/api/tasks`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(task),
    });
}

export async function apiMoveTask(id, columnId) {
    await fetch(`${API_BASE}/api/tasks/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ columnId }),
    });
}

export async function apiFetchTasks() {
    const res = await fetch(`${API_BASE}/api/tasks`, {
        method: "GET",
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(
            `Failed to fetch tasks (status ${res.status}): ${text || "Unknown error"}`
        );
    }

    const data = await res.json();
    return Array.isArray(data) ? data : [];
}

export async function apiDeleteTask(id) {
    const res = await fetch(`${API_BASE}/api/tasks/${id}`, {
        method: "DELETE",
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`Failed to delete task (status ${res.status}): ${text}`);
    }
}