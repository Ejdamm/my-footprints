<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$json = file_get_contents('php://input');
	$jsonarr = json_decode($json, true);
	if ($db->authenticate($jsonarr['email'], $jsonarr['password']) == 0)
	{		
		$lastId = $db->push($jsonarr['email'], $jsonarr['data']);
		$success = 0;
	}
	else
	{
		$success = -1;
		$lastId = -1;
	}
	$outjsonarr = array("success" => $success, "lastid" => $lastId);
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);
	print_r($outjson);
?>
