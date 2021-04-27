<?php
    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");

    include_once '../config/database.php';
    include_once '../classes/category.php';

    session_start();
    //if the user has not logged in, exit the script. We check if they are logged in by seeing if a session is not set or if 'loggedin' is not set
    if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
    {
        exit("Please login.");
    }

    //create a connection to our database
    $database = new Database();
    $db = $database->getConnection();

    //Create a new category object and give it the database info.
    $categories = new Category($db);

    //Get all of the entries...
    $categories = $categories->retrieveCategories();

    //store the number of rows produced from the query
    $numOfCategories = $categories->rowCount();

    //if there is atleast one category...
    if($numOfCategories > 0 )
    {
        //create an array to store the category information
        $categoryArray = array();
        $categoryArray["body"] = array();
        $categoryArray["entryCount"] = $numOfCategories;

        //get each row of data...
        while($row=$categories->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
            $category = array(
                "id" => $id,
                "name" => $name
            );

            //pcategory on the top of the array
            array_push($categoryArray["body"], $category);
        }

        //return the array
        echo json_encode($categoryArray);
    } else {
        exit("Apparently there are no categories! Must be a connection issue. Or maybe someone deleted the table... That wouldn't be good.");
    }

?>