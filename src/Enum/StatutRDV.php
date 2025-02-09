<?php

namespace App\Enum;

enum RDVstatut: string
{
    case CONFIRMER = 'confirmer';
    case ANNULER = 'annuler';
    case REPORTER = 'reporte';
}
