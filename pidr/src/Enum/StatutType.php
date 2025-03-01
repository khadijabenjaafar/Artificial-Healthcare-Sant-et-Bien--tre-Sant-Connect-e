<?php

namespace App\Enum;

enum StatutType: string
{
    case CONFIRME = 'confirme';
    case ANNULE = 'annule';
    case REPORTE = 'reporte';
    public static function getValues(): array
    {
        return [
            'Confirmé' => self::CONFIRME,
            'Annulé' => self::ANNULE,
            'Reporté' => self::REPORTE,
        ];
    }
    public function label(): string
    {
        return match($this) {
            self::CONFIRME => 'Confirmé',
            self::ANNULE => 'Annulé',
            self::REPORTE => 'Reporté',
        };
    }
}
