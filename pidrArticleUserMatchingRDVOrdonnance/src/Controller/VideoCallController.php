<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;

class VideoCallController extends AbstractController
{
    
    #[Route(path: '/video-call', name: 'video_call')]
     
    public function videoCall()
    {
        return $this->render('video_call/video-call.html.twig');
    }
}
