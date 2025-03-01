import face_recognition
import sys
import json
import os

# Chargement des images
image_path = sys.argv[1]  # Image envoyée par l'utilisateur
known_image_path = sys.argv[2]  # Image de référence en base

# Vérification de l'existence des fichiers
if not os.path.exists(image_path):
    print(json.dumps({"error": f"File not found: {image_path}"}))
    sys.exit(1)

if not os.path.exists(known_image_path):
    print(json.dumps({"error": f"File not found: {known_image_path}"}))
    sys.exit(1)

# Chargement et encodage des images
known_image = face_recognition.load_image_file(known_image_path)
unknown_image = face_recognition.load_image_file(image_path)

known_encodings = face_recognition.face_encodings(known_image)
unknown_encodings = face_recognition.face_encodings(unknown_image)

# Vérifier si les encodages sont disponibles
if len(known_encodings) > 0 and len(unknown_encodings) > 0:
    known_encoding = known_encodings[0]
    unknown_encoding = unknown_encodings[0]

    # Comparaison des visages
    results = face_recognition.compare_faces([known_encoding], unknown_encoding)
    match = results[0]
else:
    match = False

# Retour du résultat en JSON
print(json.dumps({"match": str(match).lower()}))