/**
 * service.js 
 * Initialise the service with a valid this.configuration structure.
 *  
 * Configuration:-
 *  
 *  loadData         = URL for a get service to load the data object
 *  loadFirstData    = URL for a get service to load the first page of data objects
 *  loadPrevData     = URL for a get service to load the previous page of data objects
 *  loadNextData     = URL for a get service to load the next page of data objects
 *  loadLastData     = URL for a get service to load the last page of data objects
 *  getData          = function to be called with the data object once it has been loaded
 *  saveData         = URL for a post service to save the data object
 *  messageScope     = Scope of fields to write error messages to
 *  lockScope        = Scope of fields to be locked during save or load operations
 *  
 */
function Service(cfg) {

	this.config = cfg;
	this.paging = "";
	this.cursor = null;
	this.parameters = "";

	/**
	 * Validate Configuration
	 */
	if (this.config.loadData == null && this.config.saveData == null) {
		alert("Service does not use loadData or saveData");
		return;
	} else if (this.config.loadData != null && this.config.getData == null) {
		alert("Service uses loadData but has not getData");
		return;
	}
		
	var that = this;

	/**
	 * Set Load Parameters
	 */
	this.setLoadParameters = function(parameters) {
		this.parameters = parameters; 
	}

	/**
	 * Load Data from the server and populate input fields.
	 */
	this.loadData = function() {

		if (this.config.loadData == null) {
			alert("Service has no loadData URL");
			return;
		} else if (this.config.getData == null) {
			alert("Service has no getData function");
			return;
		}

		var params = this.parameters;
		if (params != "") {
			params = "&" + params;
		}
		this.lockFields();										//lock UI text fields
		$.ajax({												//get data object from URL
			type: 'GET',
			url: this.config.loadData + "?" + this.paging + params,
			success: function(result, status) {					//handle success
				var data = $.parseJSON(result);
				that.config.getData(data);							//call getData
				that.cursor = data.cursor;
				that.unlockFields();							//unlock UI text fields
				that.clearMessage();
				that.pagingOptions(data);
			},
			error: that.handleErrors
		});
	
	}
	
	/**
	 * Load first page of Data from the server and populate input fields.
	 */
	this.refresh = function() {
		that.paging = ""; 
		if (that.cursor != null) {
			that.paging += "first=true";
		}
		that.loadData();
	}
		
	/**
	 * Load first page of Data from the server and populate input fields.
	 */
	this.loadFirstData = function() {
		if (!backwardOk) {
			return;
		}
		that.refresh();
	}
		
	/**
	 * Load previous page of Data from the server and populate input fields.
	 */
	this.loadPrevData = function() {
		if (!backwardOk) {
			return;
		}
		that.paging = ""; 
		if (that.cursor != null) {
			that.paging += "prev=" + that.cursor;			//use previous cursor if provided
		}
		that.loadData();
	}
		
	/**
	 * Load next page of Data from the server and populate input fields.
	 */
	this.loadNextData = function() {
		if (!forwardOk) {
			return;
		}
		that.paging = ""; 
		if (that.cursor != null) {
			that.paging += "next=" + that.cursor;			//use next cursor if provided
		}
		that.loadData();
	}

	/**
	 * Load next page of Data from the server and populate input fields.
	 */
	this.loadLastData = function() {
		if (!forwardOk) {
			return;
		}
		that.paging = ""; 
		if (that.cursor != null) {
			that.paging += "last=true";
		}
		that.loadData();
	}
		
	/**
	 * Setup the data object and save it to the Server.
	 */
	this.saveData = function(data) {
		if (that.config.saveData == null) {
			alert("Service has no saveData URL");
			return;
		}
		var params = this.parameters;
		if (params != "") {
			params = "&" + params;
		}
		that.lockFields();										//lock UI text fields
		$.ajax({												//post data object to URL
			type: 'POST',
			url: that.config.saveData + "?" + this.paging + params,
			data: JSON.stringify(data),		 
			success: function(result, status) {					//handle success
				var data = $.parseJSON(result);
				if (that.config.getData != null) {
					that.config.getData(data);					//call getData if set
				}
				that.cursor = data.cursor;
				that.unlockFields();							//unlock UI text fields '.textField'
				that.clearMessage();
				that.pagingOptions(data);
			},
			error: that.handleErrors
		});

	}

	/**
	 * Handle Errors
	 */
	this.handleErrors = function(xhr, status, error) {
		var response = $.parseJSON(xhr.responseText);
		if (!response) {
			that.showMessage("Empty server response");
			that.unlockFields();						//unlock UI text fields
		} else if (response.level) {				//handle error
			that.showMessage(response.level, response.text);
			that.unlockFields();						//unlock UI text fields
		} else if (response.temporaryurl) {	//handle temporary redirection
			var loc = window.location;
			window.location = response.temporaryurl + "?target=" + escape(loc);
		} else if (response.url) {						//handle redirection
			var loc = window.location;
			window.location = response.url
		}
	}
	
	var backwardOk;
	var forwardOk;
	/**
	 * Lock input fields.
	 */
	this.pagingOptions = function(data) {
		backwardOk = data.hasprev; 
		forwardOk = data.hasnext;
	}

	/**
	 * Lock input fields.
	 */
	this.lockFields = function() {
		if ($(that.config.lockScope)) {
			$(that.config.lockScope).attr("disabled", true);
		}
	}

	/**
	 * Unlock input fields.
	 */
	this.unlockFields = function() {
		if ($(that.config.lockScope)) {
			$(that.config.lockScope).attr("disabled", false);
		}
	}

	/**
	 * Clear message
	 */
	this.clearMessage = function() {
		
		if ($(that.config.messageScope)) {
			$(that.config.messageScope).text("");
			$(that.config.messageScope).removeClass("error");
			$(that.config.messageScope).prev().removeClass("error");
		}

	}

	/**
	 * Show a message.
	 */
	this.showMessage = function(level, text) {

		if ($(that.config.messageScope)) {
			$(that.config.messageScope).text(text);
			if ("error" == level) {
				$(that.config.messageScope).addClass("error");
				$(that.config.messageScope).prev().addClass("error");
			} else {
				$(that.config.messageScope).removeClass("error");
				$(that.config.messageScope).prev().removeClass("error");
			}
			setTimeout(function() {that.clearMessage()}, 5000);
		}

	}

}
