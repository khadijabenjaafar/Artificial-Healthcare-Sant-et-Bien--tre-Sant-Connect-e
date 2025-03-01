<?php

namespace App\Enum;

enum ModeType: string
{
    case EN_LIGNE = 'en ligne';
    case SUR_PLACE = 'sur place';
    public static function getValues(): array
    {
        return [
            'En ligne' => self::EN_LIGNE,
            'Sur place' => self::SUR_PLACE,
        ];
    }
    public function label(): string
    {
        return match($this) {
            self::EN_LIGNE => 'En ligne',
            self::SUR_PLACE => 'Sur place',
        };
    }
}
