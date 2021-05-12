import http.server
import socketserver
import os

PORT = int(os.environ["H2O_MODELS_SERVER_PORT"])
DIRECTORY = "exports"


class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)


if __name__ == "__main__":
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        httpd.serve_forever()
