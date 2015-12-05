<?php

$file = fopen("input.data", "r");
$contents = fread($file, filesize('input.data'));

$up = 0;
$down = 0;

// $exp = explode('', $contents);
for ($i = 0; $i < strlen($contents); $i++) {
	$ch = $contents[$i];
	if ($ch == '(') {
		$up++;
	} else if ($ch == ')') {
		$down++;
	}

	if ($up - $down == -1) {
		echo "Went to basement at {$i}\n";
	}
}

echo $up - $down;