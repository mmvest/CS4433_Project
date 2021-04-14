<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST");
    header("Access-Control-Max-Age: 3600");
    header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    
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