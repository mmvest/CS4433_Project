<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST");
    header("Access-Control-Max-Age: 3600");
    header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    
    include_once '../config/database.php';
    include_once '../class/user.php';

    $database = new Database();
    $db = $database->getConnection();

    //Create the new user object with a connection to the database
    $newUser = new User($db);

    //Read in the data from the post
    $data = json_decode(file_get_contents("php://input"));

    //Set variables
    $newUser->username = $data->username;
    $newUser->password = $data->password;
    $newUser->created = date('Y-m-d H:i:s');

    if($newUser->createUser())
    {
        echo 'Registered Successfully.';
    } else {
        echo 'Registration could not be completed.';
    }
?>