package com.example.api

object GoogleAppsScriptCode {
    const val SCRIPT_CODE = """/*
  ========================================================================
  RAMA TECHNICAL INSTITUTE ERP - GOOGLE APPS SCRIPT BACKEND
  ========================================================================
  Instructions for Deployment:
  1. Go to https://script.google.com/ and create a new project.
  2. Paste this entire code into your editor (e.g., Code.gs).
  3. Click "Save" and then click "Deploy" -> "New deployment".
  4. Select type: "Web app".
  5. Configure:
     - Execute as: "Me" (your-email)
     - Who has access: "Anyone" (crucial for API calls from Android)
  6. Click "Deploy", authorize the permissions, and copy the Web App URL.
  7. Paste this URL into the settings screen of the Android application.
  ========================================================================
*/

function doGet(e) {
  var action = e.parameter.action;
  if (!action) {
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Action parameter is missing"})).setMimeType(ContentService.MimeType.JSON);
  }
  
  try {
    var ss = getOrCreateSpreadsheet();
    
    if (action === "init") {
      return ContentService.createTextOutput(JSON.stringify({status: "success", message: "Sheets database initialized successfully", spreadsheetId: ss.getId()})).setMimeType(ContentService.MimeType.JSON);
    }
    
    if (action === "fetch") {
      var sheetName = e.parameter.sheet;
      if (!sheetName) {
        return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Sheet name is missing"})).setMimeType(ContentService.MimeType.JSON);
      }
      var sheet = ss.getSheetByName(sheetName);
      if (!sheet) {
        return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Sheet not found"})).setMimeType(ContentService.MimeType.JSON);
      }
      var data = getSheetData(sheet);
      return ContentService.createTextOutput(JSON.stringify({status: "success", data: data})).setMimeType(ContentService.MimeType.JSON);
    }
    
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Unknown action"})).setMimeType(ContentService.MimeType.JSON);
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: error.toString()})).setMimeType(ContentService.MimeType.JSON);
  }
}

function doPost(e) {
  try {
    var ss = getOrCreateSpreadsheet();
    var postData = JSON.parse(e.postData.contents);
    var action = postData.action;
    
    if (action === "sync") {
      var sheetName = postData.sheet;
      var rows = postData.rows; // Array of objects
      if (!sheetName || !rows) {
        return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Missing sheet or rows data"})).setMimeType(ContentService.MimeType.JSON);
      }
      
      var sheet = ss.getSheetByName(sheetName);
      if (!sheet) {
        sheet = ss.insertSheet(sheetName);
      }
      
      // Sync logic: Clear sheet and rewrite with new local database state
      sheet.clearContents();
      
      if (rows.length > 0) {
        var headers = Object.keys(rows[0]);
        sheet.appendRow(headers);
        for (var i = 0; i < rows.length; i++) {
          var rowData = [];
          for (var j = 0; j < headers.length; j++) {
            rowData.push(rows[i][headers[j]] !== undefined ? rows[i][headers[j]] : "");
          }
          sheet.appendRow(rowData);
        }
      }
      
      return ContentService.createTextOutput(JSON.stringify({status: "success", message: "Synchronized " + rows.length + " rows in " + sheetName})).setMimeType(ContentService.MimeType.JSON);
    }
    
    if (action === "upload") {
      var fileName = postData.fileName;
      val folderName = postData.folderName || "General Uploads";
      var base64Data = postData.base64Data;
      
      if (!fileName || !base64Data) {
        return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Missing fileName or base64Data"})).setMimeType(ContentService.MimeType.JSON);
      }
      
      var folder = getOrCreateFolder(folderName);
      var blob = Utilities.newBlob(Utilities.base64Decode(base64Data), postData.mimeType || "application/octet-stream", fileName);
      var file = folder.createFile(blob);
      file.setSharing(DriveApp.Access.ANYONE_WITH_LINK, DriveApp.Permission.VIEW);
      
      return ContentService.createTextOutput(JSON.stringify({
        status: "success",
        fileId: file.getId(),
        fileUrl: file.getUrl()
      })).setMimeType(ContentService.MimeType.JSON);
    }
    
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: "Unknown action"})).setMimeType(ContentService.MimeType.JSON);
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({status: "error", message: error.toString()})).setMimeType(ContentService.MimeType.JSON);
  }
}

// --- Helper Functions ---

function getOrCreateSpreadsheet() {
  var properties = PropertiesService.getScriptProperties();
  var sheetId = properties.getProperty("DATABASE_SHEET_ID");
  var ss;
  
  if (sheetId) {
    try {
      ss = SpreadsheetApp.openById(sheetId);
    } catch(e) {
      // spreadsheet might have been deleted
      sheetId = null;
    }
  }
  
  if (!sheetId) {
    ss = SpreadsheetApp.create("Rama Technical Institute ERP Database");
    properties.setProperty("DATABASE_SHEET_ID", ss.getId());
    
    // Create initial sheets with headers
    var defaultSheets = {
      "Students": ["rollNumber", "admissionNumber", "name", "fatherName", "motherName", "dob", "gender", "address", "district", "state", "pinCode", "mobile", "guardianMobile", "email", "course", "batch", "admissionDate", "photoUrl", "aadhaar", "remark"],
      "Teachers": ["id", "name", "email", "mobile", "department", "joinDate", "photoUrl", "status"],
      "Attendance": ["id", "date", "studentRollNumber", "status", "course", "batch", "markedBy"],
      "Fees": ["receiptNumber", "studentRollNumber", "courseFee", "paidAmount", "pendingAmount", "discount", "paymentDate", "paymentMode", "remarks"],
      "Courses": ["id", "name", "code", "duration", "syllabus"],
      "Batches": ["id", "name", "courseId", "timing", "teacherId"],
      "Homework": ["id", "title", "description", "course", "batch", "dueDate", "fileUrl", "teacherName", "submissionsJson"],
      "StudyMaterials": ["id", "title", "type", "url", "course", "description"],
      "Results": ["id", "studentRollNumber", "examName", "marksJson", "totalPercentage", "grade", "rank", "generatedPdfUrl"],
      "Notices": ["id", "title", "content", "date", "roleTarget", "attachmentUrl"],
      "Settings": ["key", "value"],
      "Logs": ["timestamp", "user", "action", "details"]
    };
    
    for (var name in defaultSheets) {
      var sheet = ss.getSheetByName(name);
      if (!sheet) {
        sheet = ss.insertSheet(name);
      }
      sheet.clear();
      sheet.appendRow(defaultSheets[name]);
    }
    
    // Remove the default Sheet1 if exists
    var sheet1 = ss.getSheetByName("Sheet1");
    if (sheet1) {
      ss.deleteSheet(sheet1);
    }
  }
  return ss;
}

function getOrCreateFolder(folderName) {
  var rootFolder = DriveApp.getRootFolder();
  var folders = rootFolder.getFoldersByName(folderName);
  if (folders.hasNext()) {
    return folders.next();
  } else {
    var newFolder = rootFolder.createFolder(folderName);
    newFolder.setSharing(DriveApp.Access.ANYONE_WITH_LINK, DriveApp.Permission.VIEW);
    return newFolder;
  }
}

function getSheetData(sheet) {
  var rows = sheet.getDataRange().getValues();
  if (rows.length <= 1) return [];
  
  var headers = rows[0];
  var data = [];
  
  for (var i = 1; i < rows.length; i++) {
    var row = rows[i];
    var obj = {};
    for (var j = 0; j < headers.length; j++) {
      obj[headers[j]] = row[j];
    }
    data.push(obj);
  }
  return data;
}
*/"""
}
