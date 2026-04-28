from flask import Flask, request, send_from_directory
import os

app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'

if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

files = []

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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    @app.route('/')
def home():
    return "Goohive Backend Running 🚀"
