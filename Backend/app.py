from flask import Flask, request
import os

app = Flask(__name__)

UPLOAD_FOLDER = "uploads"

if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

files = []

@app.route('/')
def home():
    return "Goohive Backend Running 🚀"

@app.route('/upload', methods=['POST'])
def upload():
    file = request.files['file']
    file.save(os.path.join(UPLOAD_FOLDER, file.filename))

    if file.filename not in files:
        files.append(file.filename)

    return "Uploaded"

@app.route('/files')
def get_files():
    return {"files": files}

@app.route('/delete/<filename>')
def delete(filename):
    path = os.path.join(UPLOAD_FOLDER, filename)

    if os.path.exists(path):
        os.remove(path)

    if filename in files:
        files.remove(filename)

    return "Deleted"

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)