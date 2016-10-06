<?php
	include 'db_connect.php';
	$db = new DB_CONNECT();
	echo file_put_contents('php://output', 'PHPME');
?>
