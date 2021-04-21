<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';
include_once '../classes/entry.php';

//start the session
session_start();

if(!isset($_SESSION) || !isset($_SESSION["loggedin"]))
{
    exit("Please login.");
}
//create a connection to the database
$database = new Database();
$db = $database->getConnection();

//create an entry object to store the id passed in and make sure it has the connection info for the database
$entry = new Entry($db);

//get the data from the request
$data = json_decode(file_get_contents("php://input"));

//set the entry_id for the entry
$entry->entry_id = $data->entry_id;

if($entry->deleteEntry())
{
    echo 'Entry deleted.';
} else {
    echo 'Failed to delete entry.';
}