<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");
    header("Access-Control-Allow-Methods: POST");
    header("Access-Control-Max-Age: 3600");
    header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

    include_once '../config/database.php';
    include_once '../classes/entry.php';

    session_start();
    //if the user has not logged in, exit the script. We check if they are logged in by seeing if a session is not set or if 'loggedin' is not set
    if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
    {
        exit("Please login.");
    }

    //create a connection to our database
    $database = new Database();
    $db = $database->getConnection();

    //Create a new entry object and give it the database info
    $newEntry = new Entry($db);

    //Read in the data from the post
    $data = json_decode(file_get_contents("php://input"));

    $newEntry->start_date_time = $data->start_date_time;
    $newEntry->end_date_time = $data->end_date_time;
    $newEntry->note = $data->note;
    $newEntry->username = $_SESSION['user'];
    $newEntry->category_name = $data->category_name;

    //call the create entry function
    if($newEntry->createEntry())
    {
        echo('Entry Created');
    } else {
        echo("Entry Creation Failed");
    }
?>