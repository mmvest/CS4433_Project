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
    $categories = new Entry($db);

    //Get data passed to the API
    $data = json_decode(file_get_contents("php://input"));

    //Set the username...
    $categories->username = $_SESSION['user'];

    //retrieve the overview
    $categories = $categories->retrieveOverview();

    $numOfCategories = $categories->rowCount();

    //if there is atleast one row in the response...
    if($numOfCategories > 0 )
    {
        //create an array to store the overview of data for each category
        $categoryArray = array();
        $categoryArray["body"] = array();
        $categoryArray["entryCount"] = $numOfCategories;

        //get each row of data...
        while($row=$categories->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
            $category = array(
                "category_name" => $category_name,
                "category_time" => $category_time,
                "percent_time" => $percent_time
            );

            //put $category on the top of the array
            array_push($categoryArray["body"], $category);
        }

        //return the array
        echo json_encode($categoryArray);
    } else {
        exit("This user has no entries.");
    }

// ?>