<?php

namespace App\Enum;

enum Statut_Facturation: string
{
    case PAYEE = 'payee';
    case EN_ATTENTE = 'en_attente';
}
