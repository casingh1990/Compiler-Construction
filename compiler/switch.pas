var
	b:array[0..9] of integer;
	a,c:integer;
	
a:= 2;
c := 0;
repeat
	begin
		c := c + 1;
		b[c-1] := c;
	end;
until c > 9;

case a of 
	1 : begin
		writeln(c);
	end;
	2 : begin
		writeln(c+1);
	end;
end.