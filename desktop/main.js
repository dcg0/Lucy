const { app, BrowserWindow, ipcMain, shell } = require('electron');
const path = require('path');
const fs = require('fs');

const logPath = path.join(app.getPath('userData'), 'hermes_logs.jsonl');

function createWindow() {
  const win = new BrowserWindow({
    width: 1280,
    height: 900,
    minWidth: 900,
    minHeight: 650,
    backgroundColor: '#020817',
    title: 'Lucy-DC',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: true
    }
  });
  win.loadFile(path.join(__dirname, 'lucy.html'));
}

ipcMain.handle('logs:append', (_event, line) => {
  try { fs.appendFileSync(logPath, String(line) + '\n', 'utf8'); return true; } catch (_) { return false; }
});
ipcMain.handle('logs:read', () => {
  try { return fs.existsSync(logPath) ? fs.readFileSync(logPath, 'utf8').trim().split('\n').slice(-100) : []; } catch (_) { return []; }
});
ipcMain.handle('logs:clear', () => {
  try { if (fs.existsSync(logPath)) fs.unlinkSync(logPath); return true; } catch (_) { return false; }
});
ipcMain.handle('logs:path', () => logPath);
ipcMain.handle('open:external', (_event, url) => {
  if (/^https?:\/\//i.test(String(url))) shell.openExternal(String(url));
});

app.whenReady().then(() => {
  createWindow();
  app.on('activate', () => { if (BrowserWindow.getAllWindows().length === 0) createWindow(); });
});
app.on('window-all-closed', () => { if (process.platform !== 'darwin') app.quit(); });
