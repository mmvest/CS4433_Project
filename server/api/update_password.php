<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST");
    header("Access-Control-Max-Age: 3600");
    header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    
    include_once '../config/database.php';
    include_once '../classes/user.php';

    //start the session, and check for logged in or not
    session_start();
    if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
    {
        exit("Please login.");
    }

    //create database connections
    $database = new Database();
    $db = $database->getConnection();

    //create the user object to store variables; pass it the db connection
    $user = new User($db);

    //Read the data posted in the request
    $data = json_decode(file_get_contents("php://input"));

    //Check if the new password was set in the request
    if(!isset($data->password))
    {
        exit('Must provide a new password. Please try again.');
    }

    //Make sure there are no spaces in password
    if($data->password !== trim($data->password) || strpos($data->password, ' ') !== false)
    {
        exit('Please do not include spaces in your new password. Password change could not be completed.');
    }

    $user->username = $_SESSION['user'];
    $user->password = $data->password;

    if($user->updatePassword())
    {
        echo 'Password change succesful.';
    } else {
        echo 'Password change could not be completed.';
    }
