<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	$json = file_get_contents('php://input');
	$jsonarr = json_decode($json, true);
	if ($db->authenticate($jsonarr['credentials']['email'], $jsonarr['credentials']['token']) == 0)
		$db->push($jsonarr['credentials']['email'], $jsonarr['data']);
?>
