<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$json = file_get_contents('php://input');
	$jsonarr = json_decode($json, true);
	if ($db->authenticate($jsonarr['email'], $jsonarr['password']) == 0)
		$db->push($jsonarr['email'], $jsonarr['data']);
?>
