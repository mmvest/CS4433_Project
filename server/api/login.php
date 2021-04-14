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

    //Read in the data from the post
    $data = json_decode(file_get_contents("php://input"));

    //Check if username and password are in the POST request
    if(!isset($data->username, $data->password))
    {
        //If we can't get the data, return error
        exit('We need both a username and password to login silly goose.');
    }

    $user->username = $data->username;
    $user->password = $data->password;


    //authenticate the user and log them in
    if($user->loginUser())
    {
        echo 'Login succesful';
    }