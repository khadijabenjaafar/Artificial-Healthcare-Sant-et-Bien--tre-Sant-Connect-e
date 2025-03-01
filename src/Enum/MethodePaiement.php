<?php

namespace App\Enum;

enum MethodePaiement: string
{
    case ESPECES = 'especes';
    case PAR_CARTE = 'par carte';

    public static function getValues(): array
    {
        return [
            'especes' => self::ESPECES,
            'par carte' => self::PAR_CARTE,
        ];
    }

    public function label(): string
    {
        return match($this) {
            self::ESPECES => 'Espèces',
            self::PAR_CARTE => 'Par Carte',
        };
    }
}
