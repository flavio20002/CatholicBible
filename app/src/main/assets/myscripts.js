function elementInViewport(el) {
	var top = el.offsetTop;
	var left = el.offsetLeft;
	var width = el.offsetWidth;
	var height = el.offsetHeight;

	while (el.offsetParent) {
		el = el.offsetParent;
		top += el.offsetTop;
		left += el.offsetLeft;
	}

	return (
		top >= window.pageYOffset &&
		left >= window.pageXOffset &&
		(top + height) <= (window.pageYOffset + window.innerHeight) &&
		(left + width) <= (window.pageXOffset + window.innerWidth)
	);
}

function firstTagViewport(tag) {
	var anchors = document.getElementsByTagName(tag);
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (elementInViewport(anchor)) {
			return anchor.innerHTML;
		}
	}
}

function avviaConfronto() {
	app.confronto(firstTagViewport('sup'));
}

function ingrandisciImmagine() {
	var elements = document.getElementsByClassName('immagine');
	var requiredElement = elements[0];
	if (requiredElement.style.width == "200%"){
		requiredElement.style.width = "100%";
		requiredElement.style.maxWidth = "400px";
	}
	else{
		requiredElement.style.width = "200%";
		requiredElement.style.maxWidth = "800px";
	}
}

function myScrollTo(e) {
	const element = document.getElementById(e);
	const elementRect = element.getBoundingClientRect();
	const absoluteElementTop = elementRect.top + window.pageYOffset;
	const top = absoluteElementTop - (window.innerHeight / 3);
	window.scrollTo(0, top);
}

function myScrollToTagHtml(tag, html) {
	var anchors = document.getElementsByTagName(tag);
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (anchor.innerHTML == html) {
			const elementRect = anchor.getBoundingClientRect();
			const absoluteElementTop = elementRect.top + window.pageYOffset;
			const top = absoluteElementTop - (window.innerHeight / 6);
			window.scrollTo(0, top);
		}
	}
}

function myScrollToTagHtmlFast(tag, html, percent) {
	var anchors = document.getElementsByTagName(tag);
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (anchor.innerHTML == html) {
			const elementRect = anchor.getBoundingClientRect();
			const absoluteElementTop = elementRect.top + window.pageYOffset;
			const top = absoluteElementTop - (window.innerHeight * percent);
			window.scrollTo(0, top);
		}
	}
}

function myScrollToPercentage(percent) {
	var body = document.body,
		html = document.documentElement;
	var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0)
	var height = Math.max(body.scrollHeight, body.offsetHeight,
		html.clientHeight, html.scrollHeight, html.offsetHeight) - h;
	window.scrollTo(0, height * percent);
}

function getScrollPercent() {
	var h = document.documentElement,
		b = document.body,
		st = 'scrollTop',
		sh = 'scrollHeight';
	return h[st] || b[st] / ((h[sh] || b[sh]) - h.clientHeight);
}

function cambiaColoreTesto(colore) {
	document.body.style.color = colore;
}

function evidenziaParola(parola) {
	var temp = document.body.innerHTML;
	var regex = new RegExp("\\b(" + parola + ")(?!>)(?!=)(?! class=)(?! id=)(?!\">)\\b", "ig");
	temp = temp.replace(regex, "<span id=\"highlighted\">$1</span>");
	document.body.innerHTML = temp
}

function cancellaEvidenziazioni() {
	var temp = document.body.innerHTML;
	var regex = new RegExp("<span id=\"highlighted\">(.*?)</span>", "g");
	temp = temp.replace(regex, "$1");
	document.body.innerHTML = temp
}

function cercaParole(parole) {
	try {
		searchArray = parole.trim().split(/\s+/);
		for (var i = 0; i < searchArray.length; i++) {
			evidenziaParola(searchArray[i]);
		}
		myScrollTo("highlighted")
	}
	catch (err) {
		console.log(err.message);
	}
}

function mostraVersetti(mostra) {
	var anchors = document.getElementsByTagName("sup");
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (!mostra) {
			anchor.style.display = "none";
		}
		else {
			anchor.style.display = "inline";
		}
	}
}

function mostraTitoli(mostra) {
	var anchors = document.getElementsByClassName("titoli");
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (!mostra) {
			anchor.style.display = "none";
		}
		else {
			anchor.style.display = "inline";
		}
	}
}

function mostraACapo(stile) {
	var anchors = document.getElementsByTagName("br");
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		if (stile == 0) {
			anchor.style.display = "inline";
		}
		else {
			anchor.style.display = "none";
		}
	}
}

function testoGiustificato(stile) {
	if (stile == 0) {
		document.body.style.textAlign = "justify";
		testoGiustificatoInterna("testosapienziali", "left");
		testoGiustificatoInterna("testosinistra", "left");
	}
	else if (stile == 1) {
		document.body.style.textAlign = "left";
		testoGiustificatoInterna("testosapienziali", "left");
		testoGiustificatoInterna("testosinistra", "left");
	}
	else {
		document.body.style.textAlign = "justify";
		testoGiustificatoInterna("testosapienziali", "justify");
		testoGiustificatoInterna("testosinistra", "justify");
	}
}

function testoGiustificatoInterna(classe, opzione) {
	var anchors = document.getElementsByClassName(classe);
	for (var i = 0; i < anchors.length; i++) {
		var anchor = anchors[i];
		anchor.style.textAlign = opzione;
	}
}