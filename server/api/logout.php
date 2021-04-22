<?php
    //start the session, and check for logged in or not
    session_start();
    if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
    {
        exit("Please login.");
    }

    session_unset();
    session_destroy();
    echo 'Logout successful.';
?>