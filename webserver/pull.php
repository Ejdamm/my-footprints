<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();

	$injson = file_get_contents('php://input');
	$injsonarr = json_decode($json, true);
	//authenticate token
	//continue if valid
	$table = $injsonarr['email'];
	$lastId = $injsonarr['lastid']; 

	//echo file_put_contents('php://output', 'PHPME'>
	//$success = array("success" => "1");
	$rows = $db->pull($table, $lastId);
	$outjsonarr = array("success" => "1", "data" => $rows);
	$outjson = json_encode($outjsonarr);

	echo "<pre>";
	print_r($outjson);
	echo "</pre>";
?>
