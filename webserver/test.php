<?php
	include 'db_connect.php';
	echo "Welcome, I am connecting Android to PHP, MySQL<br>";
	$db = new DB_CONNECT();
	
	$insert = array();
	$insert[] = array(
		"id" => 3,
		"session" => 1,
		"accessedTimestamp" => 1,
		"latitude" => 1,
		"longitude" => 1
	);
	
	//$db->push("positions", $insert);
	print_r($db->pull("positions3", 0));
?>
