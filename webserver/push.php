<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$json = file_get_contents('php://input');
	$jsonarr = json_decode($json, true);
	$db->push("positions", $jsonarr);
?>
