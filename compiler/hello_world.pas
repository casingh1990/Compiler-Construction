program help
var
	b:array[0..9] of integer;
	a,c:integer;
	
label 
	error1, skip_error;
	
	c := 0;
	repeat
		begin
			c := c + 1;
			b[c-1] := c;
			a := c mod 2;
			write(c);
			case a of 
				0 : begin
					writeln(' is Even');
				end;
				1 : begin
					writeln(' is odd');
				end;
			end;
		end;
	until c > 9;
	for a:=0; to 9 do
	begin
		write(b[a]);
		write(',');
	end;
	goto skip_error;
error1:
	writeln('Some error value');
skip_error:
end.