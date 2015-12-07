/*
 *  A tree to navigate the JSON tree provided by Personality Insights
 */
tree = new Array();
tree["Big 5 - Agreeableness - Openness - Adventurousness"]    = [0, 0, 0, 0];
tree["Big 5 - Agreeableness - Openness - Artistic interests"] = [0, 0, 0, 1];
tree["Big 5 - Agreeableness - Openness - Emotionality"]       = [0, 0, 0, 2];
tree["Big 5 - Agreeableness - Openness - Imagination"]        = [0, 0, 0, 3];
tree["Big 5 - Agreeableness - Openness - Intellect"]          = [0, 0, 0, 4];
tree["Big 5 - Agreeableness - Openness - Liberalism"]         = [0, 0, 0, 5];
tree["Big 5 - Agreeableness - Conscientiousness - Achievement striving"] = [0, 0, 1, 0];
tree["Big 5 - Agreeableness - Conscientiousness - Cautiousness"]         = [0, 0, 1, 1];
tree["Big 5 - Agreeableness - Conscientiousness - Dutifulness"]          = [0, 0, 1, 2];
tree["Big 5 - Agreeableness - Conscientiousness - Orderliness"]          = [0, 0, 1, 3];
tree["Big 5 - Agreeableness - Conscientiousness - Self-discipline"]      = [0, 0, 1, 4];
tree["Big 5 - Agreeableness - Conscientiousness - Self-efficacy"]        = [0, 0, 1, 5];
tree["Big 5 - Agreeableness - Extraversion - Activity level"] = [0, 0, 2, 0];
tree["Big 5 - Agreeableness - Extraversion - Assertiveness"]  = [0, 0, 2, 1];
tree["Big 5 - Agreeableness - Extraversion - Cheerfulness"]   = [0, 0, 2, 2];
tree["Big 5 - Agreeableness - Extraversion - Excitement-seeking"] = [0, 0, 2, 3];
tree["Big 5 - Agreeableness - Extraversion - Friendliness"]       = [0, 0, 2, 4];
tree["Big 5 - Agreeableness - Extraversion - Gregariousness"]     = [0, 0, 2, 5];
tree["Big 5 - Agreeableness - Agreeableness - Altruism"]    = [0, 0, 3, 0];
tree["Big 5 - Agreeableness - Agreeableness - Cooperation"] = [0, 0, 3, 1];
tree["Big 5 - Agreeableness - Agreeableness - Modesty"]     = [0, 0, 3, 2];
tree["Big 5 - Agreeableness - Agreeableness - Morality"]    = [0, 0, 3, 3];
tree["Big 5 - Agreeableness - Agreeableness - Sympathy"]    = [0, 0, 3, 4];
tree["Big 5 - Agreeableness - Agreeableness - Trust"]       = [0, 0, 3, 5];
tree["Big 5 - Agreeableness - Emotional range - Fiery"]                 = [0, 0, 4, 0];
tree["Big 5 - Agreeableness - Emotional range - Prone to worry"]        = [0, 0, 4, 1];
tree["Big 5 - Agreeableness - Emotional range - Melancholy"]            = [0, 0, 4, 2];
tree["Big 5 - Agreeableness - Emotional range - Immoderation"]          = [0, 0, 4, 3];
tree["Big 5 - Agreeableness - Emotional range - Self-consciousness"]    = [0, 0, 4, 4];
tree["Big 5 - Agreeableness - Emotional range - Susceptible to stress"] = [0, 0, 4, 5];
tree["Needs - Harmony - Challenge"]       = [1, 0, 0];
tree["Needs - Harmony - Closeness"]       = [1, 0, 1];
tree["Needs - Harmony - Curiosity"]       = [1, 0, 2];
tree["Needs - Harmony - Excitement"]      = [1, 0, 3];
tree["Needs - Harmony - Harmony"]         = [1, 0, 4];
tree["Needs - Harmony - Ideal"]           = [1, 0, 5];
tree["Needs - Harmony - Love"]            = [1, 0, 6];
tree["Needs - Harmony - Practicality"]    = [1, 0, 7];
tree["Needs - Harmony - Self-expression"] = [1, 0, 8];
tree["Needs - Harmony - Stability"]       = [1, 0, 9];
tree["Needs - Harmony - Structure"]       = [1, 0, 10];
tree["Values - Self-transcendence - Conservation"]       = [2, 0, 0];
tree["Values - Self-transcendence - Openness to change"] = [2, 0, 1];
tree["Values - Self-transcendence - Hedonism"]           = [2, 0, 2];
tree["Values - Self-transcendence - Self-enhancement"]   = [2, 0, 3];
tree["Values - Self-transcendence - Self-transcendence"] = [2, 0, 4];

/*
 * New book button click.
 */
$("#newBookButton").click(function() {
	
	/*
	 * Read and check the ID.
	 */
	var id = $("#bookId").val();
	if (! Number.isInteger(id * 1)) {
		/*
		 * If the ID is not a number, then empty the field and display
		 * an error message
		 */
		$("#bookId").val("");
		$("#bookId").focus();
		$("#bookAddResult").html("Invalid ID! Should be an integer.");
		$("#bookAddResult").addClass("error");
		cleanMessage();
		return;
	}
	
	$("#bookAddResult").html("Loading book..");
	// Sends a remote request to add the book.
	$.get("api/addBook", { id : id }).done(function(data) {
		if (data == "OK") {
			$("#bookAddResult").html("Book imported!");
		} else {
			$("#bookAddResult").html("There was an error. Call the sysadmin.");
			$("#bookAddResult").addClass("error");
		}
		cleanMessage();
		updateBookList();
		updatePlot();
	});
	
});

/*
 * Reset all button click.
 */
$("#resetBookButton").click(function() {
	$.get("api/resetall").done(function(data) {
		updateBookList();
		updatePlot();
	});
});

/*
 * Removes any message from the screen after 5 seconds.
 */
cleanMessage = function() {
	window.setTimeout(function() {
		$("#bookAddResult").html("");
		$("#bookAddResult").removeClass("error");
	}, 2000);
}

// Profiles provided by Watson Personality Insights
var profiles = [];
// List of book objects
var books = [];
// Book titles, indexed by book id
var titles = [];
updateBookList = function() {
	profiles = [];
	books = [];
	titles = [];
	$.get("api/listBooks").done(function(data) {
		books = JSON.parse(data);
		var html = "<tr><td><strong>ID</strong></td><td><strong>TITLE</strong></td><td><strong>AUTHOR</strong></td></tr>";
		for (i = 0; i < books.length; i++) {
			html += "<tr><td>" + books[i].id + "</td><td>" + books[i].title + "</td><td>" + books[i].author + "</td></tr>";
			
			profiles = [];
			$.ajax({
				url: "https://e7b22b18-c7f1-4888-a4ef-baf80f1beddf-bluemix.cloudant.com/book-insights/" + books[i].id,
				xhrFields: {
					withCredentials: true
				}
			}).done(function(data) {
				profiles.push(JSON.parse(data));
			});
			titles[books[i].id] = books[i].title;
		}
		$("#booksTable").html(html);
	});
}

/*
 * 
 */
find = function(x, a) {
	return traverse(x.tree, a.slice());
}

/*
 * Traverses the given JSON tree to read the percentage
 * of a characteristic mapped by the array of indices.
 */
traverse = function(tree, indices) {
	var index = indices.shift();
	/*
	 * If there is no index to navigate, then we reached
	 * the leaf and should return the percentage.
	 */
	if (index == undefined) {
		return tree.percentage;
	} else {
		// Recursive call one layer deeper.
		return traverse(tree.children[index], indices);
	}
}

/*
 * Updates the plot
 */
updatePlot = function() {
	var c = document.getElementById("plotCanvas");
	var ctx = c.getContext("2d");
	ctx.clearRect(0, 0, c.width, c.height);
	
	// Plot lines and area
	
	ctx.beginPath();
	ctx.rect(50, 50, 500, 500);
	ctx.fillStyle = '#444';
	ctx.fill();
	ctx.stroke();
	
	ctx.beginPath();
	ctx.rect(50, 50, 500, 500);
	ctx.lineWidth = 2;
	ctx.strokeStyle = '#888';
	ctx.stroke();
	
	ctx.beginPath();
    ctx.moveTo(300, 50);
    ctx.lineTo(300, 550);
    ctx.stroke();
    
	ctx.beginPath();
    ctx.moveTo(50, 300);
    ctx.lineTo(550, 300);
    ctx.stroke();
    
    // Plot labels
    
    ctx.font = '15pt Arial';
    ctx.fillStyle = '#FFF';
    ctx.fillText("0%", 50, 570);
    ctx.fillText("A", 294, 570);
    ctx.fillText("100%", 500, 570);
    ctx.fillText("A", 294, 570);
    ctx.fillText("100%", 0, 57);
    ctx.fillText("B", 35, 307);
    ctx.fillText("0%", 25, 557);
    
	var arrayA = tree[$("#cat1 option:selected").val()];
	var arrayB = tree[$("#cat2 option:selected").val()];
	
    ctx.font = '10pt Arial';
	for (i = 0; i < profiles.length; i++) {
		var valA = find(profiles[i], arrayA);
		var valB = find(profiles[i], arrayB);
		var centerX = 500 * valA + 50;
		var centerY = 550 - 500 * valB;
		ctx.beginPath();
		ctx.strokeStyle = '#FFF';
		ctx.arc(centerX - 10, centerY - 10, 20, 0, 2 * Math.PI, false);
		ctx.lineWidth = 1;
	    ctx.stroke();
		ctx.fillStyle = '#FFF';
	    ctx.fillText(titles[profiles[i].bookId], centerX + 15, centerY - 6);
	}
}

/*
 * Drop box A on change
 */
$("#cat1").change(function() {
	updatePlot();
});

/*
 * Drop box B on change
 */
$("#cat2").change(function() {
	updatePlot();
});

/*
 * Startup
 */

updateBookList();
updatePlot();
