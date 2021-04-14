<?php

    include_once '../config/database.php';
    include_once '../classes/user.php';

    //start the session
    session_start();

    $database = new Database();
    $db = $database->getConnection();

    //Create the new user object with a connection to the database
    $user = new User($db);

    //authenticate the user and log them in
    if($user->loginUser())
    {
        echo 'Login succesful';
    }