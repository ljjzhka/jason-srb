package com.happy.srb.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.api.R;
import com.happy.common.exception.BusinessException;
import com.happy.common.result.ResponseEnum;
import com.happy.common.result.Result;
import com.happy.srb.core.pojo.dto.ExcelDictDTO;
import com.happy.srb.core.pojo.entity.Dict;
import com.happy.srb.core.service.DictService;
import com.sun.org.apache.xpath.internal.operations.Mult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author LeiJJ
 * @date 2021-11-02 18:37
 */
@Api(tags = "数据字典管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Resource
    private DictService dictService;

    @ApiOperation("Excel批量导入数据字典")
    @PostMapping("/import")
    public Result importData(@ApiParam(value = "Excel文件",required = true)
                                 @RequestParam("file") MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return Result.ok().message("批量导入成功");
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @ApiOperation("Excel数据的导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response){

        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.exportData());

        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw  new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public Result listByParentId(
            @ApiParam(value = "上级节点id", required = true)
            @PathVariable Long parentId) {
        List<Dict> dictList = dictService.listByParentId(parentId);
        return Result.ok().data("dictList", dictList);
    }
}
