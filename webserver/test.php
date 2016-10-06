<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();

	$json = file_get_contents('php://input');
	$jsonarr = json_decode($json, true);
	//$tofile = print_r($jsonarr, true);

	//$myfile = fopen("logfile.txt", "w");
	//if (!empty($jsonarr)) fwrite($myfile, $tofile . "\n");
	//fclose($myfile);
	
	/*$insert = array();
	$insert[] = array(
		"id" => 12,
		"session" => 1,
		"accessedTimestamp" => 1,
		"latitude" => 1,
		"longitude" => 1
	);
	if (isset($_POST['key']))
	{
		//$db->push("positions", $insert);
	}*/
	//echo print_r($insert, true);
	$db->push("positions", $jsonarr);

	//print_r($db->pull("positions", 0));
?>
