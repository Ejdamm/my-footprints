<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();

	$injson = file_get_contents('php://input');
	$injsonarr = json_decode($injson, true);
	//authenticate token
	//continue if valid
	$table = $injsonarr['email'];
	$lastId = $injsonarr['lastid']; 

	$rows = $db->pull($table, $lastId);
	$outjsonarr = array("success" => "1", "data" => $rows);
	$outjson = json_encode($outjsonarr, JSON_FORCE_OBJECT);

	print_r($outjson);
?>
