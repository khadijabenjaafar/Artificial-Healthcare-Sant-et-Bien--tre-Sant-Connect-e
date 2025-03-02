<?php
namespace App\Enum;

enum StatutFacturation: string
{
    case EN_ATTENTE = 'en attente';
    case PAYEE = 'payée';
    case ANNULEE = 'annulée';
    case EN_COURS = 'en cours';

    public static function getValues(): array
    {
        return [
            'en attente' => self::EN_ATTENTE,
            'payée' => self::PAYEE,
            'annulée' => self::ANNULEE,
            'en cours' => self::EN_COURS,
        ];
    }

    public function label(): string
    {
        return match($this) {
            self::EN_ATTENTE => 'En attente',
            self::PAYEE => 'Payée',
            self::ANNULEE => 'Annulée',
            self::EN_COURS => 'En cours',
        };
    }
}
