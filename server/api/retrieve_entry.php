<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");

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

    //Create a new entry object and give it the database info.
    $entries = new Entry($db);

    //Get data passed to the API
    $data = json_decode(file_get_contents("php://input"));
    $entries->start_date_time = $data->start_date_time ?? NULL;
    $entries->end_date_time = $data->end_date_time ?? NULL;
    $entries->note = $data->note ?? NULL;
    $entries->category_name = $data->category_name ?? NULL;

    //Set the username...
    $entries->username = $_SESSION['user'];

    //Get all of the entries...
    $entries = $entries->retrieveEntries();

    $numOfEntries = $entries->rowCount();

    //if there is atleast one entry...
    if($numOfEntries > 0 )
    {
        //create an array to store the entry information
        $entryArray = array();
        $entryArray["body"] = array();
        $entryArray["entryCount"] = $numOfEntries;

        //get each row of data...
        while($row=$entries->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
            $entry = array(
                "entry_id" => $entry_id,
                "start_date_time" => $start_date_time,
                "end_date_time" => $end_date_time,
                "note" => $note,
                "username" => $username,
                "category_name" => $category_name
            );

            //put $entry on the top of the array
            array_push($entryArray["body"], $entry);
        }

        //return the array
        echo json_encode($entryArray);
    } else {
        exit("No entries match those conditions.");
    }

?>