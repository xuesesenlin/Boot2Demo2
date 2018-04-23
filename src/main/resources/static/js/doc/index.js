$(function(){
    init();
});

//init
function init(){
    var name = $('#name').text();
    $.ajax({
        url:path+'/v2/api-docs',
        cache:false,
        success:function(data){
            var h = '';
            for (var key in data.paths) {
                var data2 = data.paths[key];
                for (var key2 in data2) {
                    h += menu_html(key,key2,data2[key2].summary,data2[key2].consumes,data2[key2].produces,data2[key2].parameters);
                    $(data2[key2].parameters).each(function(index,e){
                        alert(e.name+","+e.required);
                    });
                }
            }
            $('#table_data').append(h);
        }
    }).done(function(d){
    }).fail(function() {
        alert('维护中');
    }).always(function() {
    });
}

//模版
function menu_html(a,b,c,d,e,f){
    var h = '<tr onclick="get(\''+f+'\')">'
            +'    <td>'+a+'</td>'
            +'    <td>'+b+'</td>'
            +'    <td>'+c+'</td>'
            +'    <td>'+d+'</td>'
            +'    <td>'+e+'</td>'
            +'    <td>'+f+'</td>'
            +'</tr>';
    return h;
}

//模版
function menu_html2(a,b,c){
    var h = '<tr>'
            +'    <td>'+a+'</td>'
            +'    <td>'+b+'</td>'
            +'    <td>'+c+'</td>'
            +'</tr>';
    return h;
}

//查看参数
//function get(object){
//alert($.type(object));
//$(object).each(function(index,e){
//    alert(e.name);
//});
//    $('#table_data2').find('tr').remove();
//    alert($.type(object));
//    for(var i=0;i<object.length;i++){
//       alert(object[i].name);
//    }
//        var h = menu_html2(object.name,2,key);
//        $('#table_data2').append(h);
//}

//    error
function erryFunction() {
    $('.htmlBody1').hide();
    $('.htmlBody2').show();
    alert("错误!");
};