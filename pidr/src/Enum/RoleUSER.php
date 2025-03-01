<?php

namespace App\Enum;

enum enumRole: string
{
    case FREELANCER = 'ROLE_FREELANCER';
    case PATIENT = 'ROLE_PATIENT';
    case MEDECIN = 'ROLE_MEDECIN';
    case PHARMACIEN = 'ROLE_PHARMACIEN';
    case ADMIN = 'ROLE_ADMIN';
}
