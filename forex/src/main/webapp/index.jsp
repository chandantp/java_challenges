<html>
<head>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script>
	var quoteCurrency = "ALL";
	var baseCurrency = "USD";
	
	$(document).ready(function() {		
		//
		// Exchange Rates section
		//	
		function loadExchangeRatesSection() {

			$.ajax({url: "http://localhost:8080/forex/fxapi/currencies", success: function(result) {
				
				$("#baseCurrencies").find("option").remove();
				$("#currencies").find("option").remove();
				$('#currencies').append('<option value="ALL">ALL</option>');
				
				for(x=0; x<result.length; x++) {
					$('#currencies').append($('<option>', {
				        value: result[x].code,
				        text : result[x].code 
				    }));
					$('#baseCurrencies').append($('<option>', {
				        value: result[x].code,
				        text : result[x].code 
				    }));
				}
				
				$('#currencies option[value=' + quoteCurrency).attr('selected', 'selected');
				$('#baseCurrencies option[value=' + baseCurrency).attr('selected', 'selected');
		    }});
			
			displayExchangeRates();
			
			$("#converter").hide();
			$("#divxrates").show();
		}
		
		function displayExchangeRates() {
			$("#txrates").find("tr:gt(0)").remove();
			var urlstr = "http://localhost:8080/forex/fxapi/xrates?base=" + baseCurrency;
			if (quoteCurrency != "ALL") {
				urlstr += "&quote=" + quoteCurrency;
			}
			$.ajax({url: urlstr, success: function(result){
				for(x=0; x<result.exchangeRates.length; x++) {
					var xrate = result.exchangeRates[x].rate.toFixed(6);
					var row = '<tr><td>' + result.exchangeRates[x].code + '</td><td class="floats">' + xrate + '</td></tr>';
					$('#txrates tr:last').after(row);
				}
		    }});	
		}
		
		$("#xratesLink").click(function() {
			$(this).addClass("active");
			$("#converterLink").removeClass("active");
			loadExchangeRatesSection();
		});
		
		$("#xratesRefresh").click(function() {
			loadExchangeRatesSection();
		});
		
		$("#currencies").change(function(){
			quoteCurrency = $("#currencies").val();
			displayExchangeRates();
		});
		
		$("#baseCurrencies").change(function(){
			baseCurrency = $("#baseCurrencies").val();
			displayExchangeRates();
		});		
		
		//
		// Currency Converter section
		//		
		function loadCurrencyConverterSection() {
			
			$.ajax({url: "http://localhost:8080/forex/fxapi/currencies", success: function(result){
				
				$("#sourceCurrency").find("option").remove();
				$("#targetCurrency").find("option").remove();
				
				for(x=0; x<result.length; x++) {
					$('#sourceCurrency').append($('<option>', { 
				        value: result[x].code,
				        text : result[x].code 
				    }));
					$('#targetCurrency').append($('<option>', { 
				        value: result[x].code,
				        text : result[x].code 
				    }));
				}
				
				$("#sourceCurrency").val(result[0].code);
				$("#targetCurrency").val(result[1].code);
				$("#sourceAmount").val(1);
				$("#sourceAmount").keyup();				
		    }});
			
			$("#converter").show();
			$("#divxrates").hide();
		}
		
		function covertCurrency() {
			var source = $("#sourceCurrency").val();
			var target = $("#targetCurrency").val();
			var amount = $("#sourceAmount").val();
			var urlstr = "http://localhost:8080/forex/fxapi/currency-converter?source="+source+"&target="+target+"&amount="+amount;
			$.ajax({url: urlstr, success: function(result){
				$("#targetAmount").val(result);
		    }});
		}
		
		$("#converterLink").click(function() {
			$(this).addClass("active");
			$("#xratesLink").removeClass("active");
			loadCurrencyConverterSection();
		});
		
		$("#converterRefresh").click(function() {			
			loadCurrencyConverterSection();
		});
		
		$("#sourceAmount").change(function(){
			covertCurrency();
		});
		
		$("#sourceAmount").keyup(function(){
			covertCurrency();
		});
		
		$("#sourceCurrency").change(function(){
			covertCurrency();
		});
		
		$("#targetCurrency").change(function(){
			covertCurrency();
		});
		
		// Show exchange rates section first
		$("#xratesLink").click();
	});
	
</script>
</head>

<body>
	<div id="header">
		<h1>A Simple Forex App!</h1>
	</div>

	<div id="navigation">
		<table>
			<tr><td><a id="xratesLink" href="#">Exchange Rates</a></td></tr>
			<tr><td><a id="converterLink" href="#">Currency Converter</a></td></tr>
		</table>
	</div>

	<div id="divxrates">
		<h2>Exchange Rates</h2>
		<table id="txrates">
			<tr>
				<th><select id="currencies"></select></th>
				<th>Per Unit Rate in <select id="baseCurrencies"></select></th>
			</tr>
		</table>
		<br>
		<button id="xratesRefresh">Refresh</button>
	</div>
	<div id="converter">
		<h2>Currency Converter</h2>
		<table id="tconverter">
			<tr>
				<td><input id="sourceAmount" type="number" /></td>
				<td><select id="sourceCurrency"></select></td>
			</tr>
			<tr>
				<td><input id="targetAmount" type="number" step="0.000001" readonly/></td>
				<td><select id="targetCurrency"></select></td>
			</tr>
		</table>
		<br>
		<button id="converterRefresh">Refresh</button>
	</div>

	<div id="footer">Designed and Developed by Chandan</div>
</body>
</html>