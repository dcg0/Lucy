const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('LucyBridge', {
  appendLog: (line) => ipcRenderer.invoke('logs:append', line),
  readLogs: () => ipcRenderer.invoke('logs:read'),
  clearLogs: () => ipcRenderer.invoke('logs:clear'),
  logsPath: () => ipcRenderer.invoke('logs:path')
});
