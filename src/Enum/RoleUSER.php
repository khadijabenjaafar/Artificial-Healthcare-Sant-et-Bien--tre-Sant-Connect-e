<?php

namespace App\Enum;

enum enumRole: string
{
    case MEDECIN = 'medecin';
    case PATIENT = 'patient';
    case PHARMACIE = 'pharmacie';
    case FREELANCER = 'freelancer';
}
