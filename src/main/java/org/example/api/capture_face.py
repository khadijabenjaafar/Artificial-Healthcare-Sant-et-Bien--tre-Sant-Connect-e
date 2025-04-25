import cv2
import sys
import os

# Vérifie si un nom de fichier a été passé en argument
if len(sys.argv) < 2:
    print("Erreur : Nom de fichier requis en argument.")
    sys.exit(1)

# Récupérer le chemin de sauvegarde passé depuis Java
output_path = sys.argv[1]

# Créer le dossier s'il n'existe pas
output_dir = os.path.dirname(output_path)
os.makedirs(output_dir, exist_ok=True)

# Ouvrir la webcam
cap = cv2.VideoCapture(0)
if not cap.isOpened():
    print("Erreur : Impossible d'accéder à la webcam.")
    sys.exit(1)

# Lire une image
ret, frame = cap.read()
if ret:
    # Sauvegarder l'image
    cv2.imwrite(output_path, frame)
    print(f"Image sauvegardée à : {output_path}")
else:
    print("Erreur : Impossible de capturer une image.")
    sys.exit(1)

cap.release()
