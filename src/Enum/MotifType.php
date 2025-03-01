<?php

namespace App\Enum;

enum MotifType: string
{
    case CONTROLE = 'controle';
    case SUIVI = 'suivi';
    case URGENCE = 'urgence';
    public static function getValues(): array
    {
        return [
            'Contrôle' => self::CONTROLE,
            'Suivi' => self::SUIVI,
            'Urgence' => self::URGENCE,
        ];
    }
    public function label(): string
    {
        return match($this) {
            self::CONTROLE => 'Contrôle',
            self::SUIVI => 'Suivi',
            self::URGENCE => 'Urgence',
        };
    }
}
