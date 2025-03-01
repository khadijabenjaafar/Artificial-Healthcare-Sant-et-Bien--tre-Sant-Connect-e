<?php

namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class SpeechifyService
{
    private HttpClientInterface $httpClient;
    private string $apiKey;

    public function __construct(HttpClientInterface $httpClient)
    {
        $this->httpClient = $httpClient;
        $this->apiKey = '0k125QJwSJJLV4V4tbFaLFgzZ2-s-bsCfGlUXb626F0='; // 🔹 Remplace par ta clé API Speechify
    }

    /**
     * Génère un fichier MP3 depuis Speechify et le convertit en MP4 avec une image fixe.
     *
     * @param string $text  Le texte à convertir en audio
     * @param string $voice La voix à utiliser (ex: "clinton")
     * @param string $format Le format de sortie (ex: "mp3")
     * @return array Contient les chemins du fichier MP3 et MP4 générés
     */
    public function generateSpeechVideo(string $text, string $voice = 'clinton', string $format = 'mp3'): array
    {
        $url = 'https://api.sws.speechify.com/v1/audio/speech';

        try {
            // 🔹 Envoyer la requête à Speechify pour générer l'audio
            $response = $this->httpClient->request('POST', $url, [
                'headers' => [
                    'Authorization' => 'Bearer ' . $this->apiKey,
                    'Accept' => 'application/json',
                    'Content-Type' => 'application/json',
                ],
                'json' => [
                    'input' => $text,
                    'language' => 'en-US',
                    'model' => 'simba-base',
                    'voice_id' => $voice,
                    'audio_format' => $format
                ],
            ]);

            $data = json_decode($response->getContent(), true);

            if (isset($data['audio_data'])) {
                $audioBase64 = $data['audio_data'];

                // 🔹 Enregistrer le fichier MP3
                $mp3Path = 'audio_output.mp3';
                file_put_contents($mp3Path, base64_decode($audioBase64));

                // 🔹 Convertir le MP3 en MP4 avec une image de fond
               // $mp4Path = 'public/audio_output.mp4';
                //$imagePath = 'public/background.png'; // 🔹 Assure-toi que cette image existe

                // Vérifier si FFmpeg est installé
                //if (!shell_exec("which ffmpeg")) {
                 //   throw new \Exception("FFmpeg n'est pas installé. Installe-le avec 'brew install ffmpeg' sur Mac.");
                //}

                // Commande FFmpeg pour générer le MP4
                //$ffmpegCommand = "ffmpeg -loop 1 -i $imagePath -i $mp3Path -c:v libx264 -c:a aac -b:a 192k -shortest $mp4Path";
                //shell_exec($ffmpegCommand);

                return [
                    'mp3' => $mp3Path,
                    //'mp4' => $mp4Path
                ];
            }

            throw new \Exception("Erreur lors de la génération de l'audio.");
        } catch (\Exception $e) {
            throw new \Exception("Erreur API Speechify : " . $e->getMessage());
        }
    }
}