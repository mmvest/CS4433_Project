<?php

    include_once '../config/database.php';
    include_once '../classes/user.php';

    //start the session
    session_start();
    if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
    {
        exit("Please login.");
    }

    //create a connection to the database
    $database = new Database();
    $db = $database->getConnection();

    //create user object, pass in connection information
    $user = new User($db);

    //get the data from the request
    $data = json_decode(file_get_contents("php://input"));

    //set the username and password for the user
    $user->username = $_SESSION['user'];
    $user->password = $data->password;

    if($user->deleteUser())
    {
        session_unset();
        session_destroy();
        echo 'User deleted. Logout Successful.';
    } else {
        echo 'Failed to delete user.';
    }
?>