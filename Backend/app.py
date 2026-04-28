from flask import Flask, request
import os

app = Flask(__name__)

UPLOAD_FOLDER = "uploads"

if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

files = []

# 🌐 HOME PAGE (Chrome UI)
@app.route('/')
def home():
    return '''
    <h2>Goohive Cloud Sync Manager</h2>

    <form action="/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="file">
        <button type="submit">Upload</button>
    </form>

    <br><br>

    <a href="/files">View Uploaded Files</a>
    '''

# 📤 UPLOAD
@app.route('/upload', methods=['POST'])
def upload():
    file = request.files['file']
    filepath = os.path.join(UPLOAD_FOLDER, file.filename)
    file.save(filepath)

    if file.filename not in files:
        files.append(file.filename)

    return '''
    <h3>File Uploaded Successfully ✅</h3>
    <a href="/">Go Back</a>
    '''

# 📂 VIEW FILES (Browser UI)
@app.route('/files')
def get_files():
    file_list = "<h3>Uploaded Files:</h3><ul>"

    for f in files:
        file_list += f'''
        <li>
            {f}
            <a href="/delete/{f}" style="margin-left:10px;">Delete</a>
        </li>
        '''

    file_list += "</ul><br><a href='/'>Go Back</a>"

    return file_list

# ❌ DELETE FILE
@app.route('/delete/<filename>')
def delete(filename):
    path = os.path.join(UPLOAD_FOLDER, filename)

    if os.path.exists(path):
        os.remove(path)

    if filename in files:
        files.remove(filename)

    return '''
    <h3>File Deleted ❌</h3>
    <a href="/files">Back to Files</a>
    '''

# ▶️ RUN SERVER (Render compatible)
if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)