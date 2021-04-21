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

//create an entry object to store the variables passed in and make sure it has the connection info for the database
$entry = new Entry($db);

//get the data from the request
$data = json_decode(file_get_contents("php://input"));
$entry->start_date_time = $data->start_date_time ?? NULL;
$entry->end_date_time = $data->end_date_time ?? NULL;
$entry->note = $data->note ?? NULL;
$entry->category_name = $data->category_name ?? NULL;
$entry->entry_id = $data->entry_id ?? die('Failed to update entry. Must provide an entry id.');

if($entry->updateEntry())
{
    echo 'Entry updated.';
} else {
    echo 'Failed to update entry.';
}